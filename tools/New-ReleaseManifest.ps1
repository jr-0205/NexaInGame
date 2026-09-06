param(
    [Parameter(Mandatory = $true)][string]$ArtifactsDirectory,
    [Parameter(Mandatory = $true)][string]$OutputPath,
    [Parameter(Mandatory = $true)][string]$ReleaseTag,
    [string]$Repository = 'jr-0205/NexaInGame'
)

$ErrorActionPreference = 'Stop'
$resolvedArtifacts = (Resolve-Path -LiteralPath $ArtifactsDirectory).Path
$version = $ReleaseTag.TrimStart('v')
$publishedAt = (Get-Date).ToUniversalTime().ToString('o')
$supportedTargets = @('1.21.1', '1.21.8', '1.21.10')

$artifacts = foreach ($target in $supportedTargets) {
    $jar = Get-ChildItem -LiteralPath $resolvedArtifacts -File -Filter "nexa-ingame-fabric-$target-*.jar" |
        Where-Object Name -NotMatch '-sources\.jar$' |
        Sort-Object Name |
        Select-Object -First 1
    if (-not $jar) { continue }

    [ordered]@{
        minecraftVersion = $target
        loader = 'Fabric'
        nexoInGameVersion = $version
        status = 'published'
        fileName = $jar.Name
        relativePath = $null
        downloadUrl = "https://github.com/$Repository/releases/download/$ReleaseTag/$($jar.Name)"
        sha256 = (Get-FileHash -LiteralPath $jar.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
        size = $jar.Length
        java = 21
        publishedAt = $publishedAt
        dependencies = @(
            [ordered]@{
                source = 'modrinth'
                projectId = 'fabric-api'
                projectType = 'mod'
                detectPattern = 'fabric-api*.jar'
            }
        )
    }
}

if ($artifacts.Count -eq 0) {
    throw 'No se encontraron JAR ejecutables compatibles para generar el manifiesto.'
}

$manifest = [ordered]@{
    schemaVersion = 1
    product = 'nexa-ingame'
    generatedAt = $publishedAt
    artifacts = @($artifacts)
}

$parent = Split-Path -Parent $OutputPath
if ($parent) { New-Item -ItemType Directory -Force -Path $parent | Out-Null }
$json = $manifest | ConvertTo-Json -Depth 8
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText([System.IO.Path]::GetFullPath($OutputPath), $json, $utf8WithoutBom)
