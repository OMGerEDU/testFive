param (
    [string]$projectName,
    [string]$groupId,
    [string]$repoName
)

if (!$projectName -or !$groupId -or !$repoName) {
    Write-Host "Welcome to the Java project initialization script."
    $projectName = Read-Host "Please enter the project name"
    $groupId = Read-Host "Please enter the groupId"
    $repoName = Read-Host "Please enter the GitHub repository name - without spaces!"
}

# Create a new Maven project
mvn archetype:generate "-DgroupId=$groupId" "-DartifactId=$projectName" "-DarchetypeArtifactId=maven-archetype-quickstart" "-DinteractiveMode=false"

Set-Location $projectName

# Create a .github/workflows folder
New-Item -ItemType Directory -Path ".github/workflows"

# Create a GitHub Actions workflow file
@"
name: Update Versioning Scripts

on:
  repository_dispatch:
    types: [update-versioning-scripts]

jobs:
  update:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Checkout autoVersioningScripts repository
        uses: actions/checkout@v2
        with:
          repository: OMGerEDU/autoVersioningScripts
          token: $${{ secrets.ACCESS_TOKEN }}
          path: autoVersioningScripts

      - name: Copy versioning scripts
        run: |
          cp -r autoVersioningScripts/scripts/* scripts/
          git add scripts/
          git config user.email "github-action@example.com"
          git config user.name "GitHub Action"
          git commit -m "Update versioning scripts"
          git push

"@ | Set-Content -Path ".github/workflows/updateVersions.yml"

# Initialize Git repository
git init

# Commit the initial project setup
git add .
git commit -m "Initial project setup"

# Create a new private GitHub repository
gh repo create $repoName --private --add-readme

# Set up the "autoversioning-dependent" tag for the repository
$githubUsername = $env:GITHUB_USERNAME
$token = $env:GH_TOKEN
$headers = @{
    "Authorization" = "token $token"
    "Accept"        = "application/vnd.github.mercy-preview+json"
}
$body = @{
    "names" = @("autoversioning-dependent")
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://api.github.com/repos/$githubUsername/$repoName/topics" -Method PUT -Headers $headers -Body $body

# Get the public key for the repository
$publicKeyResponse = Invoke-RestMethod -Uri "https://api.github.com/repos/$githubUsername/$repoName/actions/secrets/public-key" -Method GET -Headers $headers
$publicKeyId = $publicKeyResponse.key_id
$publicKey = $publicKeyResponse.key

# Encrypt the access token using the RSA public key
function EncryptData {
    param(
        [string]$data,
        [string]$publicKey
    )

    $rsa = [System.Security.Cryptography.RSACryptoServiceProvider]::new()
    $rsa.ImportFromPem($publicKey)
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($data)
    $encryptedBytes = $rsa.Encrypt($bytes, $true)
    return [Convert]::ToBase64String($encryptedBytes)
}

$encryptedAccessToken = EncryptData -data $env:ACCESS_TOKEN -publicKey $publicKey

# Create a new secret in the GitHub repository
$secretName = "ACCESS_TOKEN"
$secretBody = @{
    "encrypted_value" = $encryptedAccessToken
    "key_id"          = $publicKeyId
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://api.github.com/repos/$githubUsername/$repoName/actions/secrets/$secretName" -Method PUT -Headers $headers -Body $secretBody

# Push the project to the new GitHub repository
git remote add origin "https://github.com/$githubUsername/$repoName.git"
git branch -M main
git pull origin main --allow-unrelated-histories
git push -u origin main

# Remove the previous README.md file
git rm README.md
git commit -m "Remove default README.md"
git push

# Create a temporary folder to clone the "scripts" repository
New-Item -ItemType Directory -Path "temp_scripts"

# Clone only the "scripts" folder from the remote repository into the temporary folder
git clone --depth 1 --filter=blob:none --sparse "https://github.com/OMGerEDU/autoVersioningScripts.git" temp_scripts
Set-Location temp_scripts
git sparse-checkout set scripts

# Move the cloned "scripts" folder into the project folder
Set-Location ..
Move-Item -Path "temp_scripts/scripts" -Destination "scripts"
Remove-Item -Recurse -Force "temp_scripts"

# Stage the "scripts" folder and commit the changes
git add scripts
git commit -m "Add scripts directory"
git push

Write-Host "The Java project '$projectName' has been successfully initialized and pushed to the '$repoName' GitHub repository."