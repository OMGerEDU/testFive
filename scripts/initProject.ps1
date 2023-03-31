# Get project name and groupId from the user
Write-Host "Welcome to the Java project initialization script."
[string]$projectName = Read-Host -Prompt 'Please enter the project name'
[string]$groupId = Read-Host -Prompt 'Please enter the groupId'

# Create project directory and move into it
New-Item -Path $projectName -ItemType Directory
Set-Location -Path $projectName

# Run Maven archetype to generate a basic Java project
mvn archetype:generate -DgroupId="${groupId}" -DartifactId="${projectName}" -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

# Move generated project files to the root directory
Move-Item -Path "$projectName\*" -Destination . -Force
Remove-Item -Path $projectName -Recurse -Force

# Replace placeholders with actual values in generated files
$sourcePath = "src\main\java\$($groupId -replace '\.', '\\')"
$testPath = "src\test\java\$($groupId -replace '\.', '\\')"

(Get-Content -Path "pom.xml") -replace '\$projectName', $projectName -replace '\$groupId', $groupId | Set-Content -Path "pom.xml"
(Get-Content -Path "$sourcePath\App.java") -replace '\$groupId', $groupId | Set-Content -Path "$sourcePath\App.java"
(Get-Content -Path "$testPath\AppTest.java") -replace '\$groupId', $groupId | Set-Content -Path "$testPath\AppTest.java"

# Git and scripts initialization
New-Item -Path ".github" -ItemType Directory
New-Item -Path ".github\workflows" -ItemType Directory

# Add GitHub Actions workflow
$workflow = @"
name: Update scripts

on:
  workflow_dispatch:

jobs:
  update-scripts:
    runs-on: ubuntu-latest
    steps:
      - name: Check out repository
        uses: actions/checkout@v2
      - name: Set up Python
        uses: actions/setup-python@v2
        with:
          python-version: '3.x'
      - name: Update scripts
        run: |
          pip install requests
          python path/to/your/update_scripts.py
"@

Set-Content -Path ".github\workflows\update_scripts.yml" -Value $workflow

git init
git add .
git commit -m "Initial project setup"

New-Item -Path "scripts" -ItemType Directory
Set-Location "scripts"
git clone https://github.com/OMGerEDU/autoVersioningScripts.git temp_scripts
Copy-Item -Path "temp_scripts\*" -Destination . -Recurse -Force
if (Test-Path -Path "temp_scripts") {
    Remove-Item -Path "temp_scripts" -Recurse -Force
}
git add .
git commit -m "Add scripts directory"
Set-Location ..

Write-Host "Project '$projectName' has been initialized successfully."