# Read the configuration file
$config = Get-Content "config.txt" -Raw | ConvertFrom-Json

# Change the current working directory to the parent directory
cd ..


# Remove all existing remotes from the current Git repository
$remotes = git remote
foreach ($remote in $remotes) {
    git remote rm $remote
}


# Set the variables from the configuration file
$githubToken = $env.TOKEN
# Define your GitHub repository information
$githubUsername = $env.GITHUB_USERNAME
if (args[0] -eq $null)
{
    Write-Output "Enter repo name to create new repo"
    return
}

$repoName = $args[0]
$repoDescription = $config.description
$repoIsPrivate = $config.isPrivate

# Create a new GitHub repository using the GitHub API
$headers = @{
    Authorization = "Token $githubToken"
    Accept = "application/vnd.github.v3+json"
}
$body = @{
    name = $repoName
    description = $repoDescription
    private = $repoIsPrivate
}
Invoke-RestMethod -Method Post -Uri "https://api.github.com/user/repos" -Headers $headers -Body ($body | ConvertTo-Json)

# Initialize the local folder as a Git repository
git init

# Add all files in the root folder to the Git repository
$rootFolder = Split-Path -Path $MyInvocation.MyCommand.Path -Parent
Set-Location $rootFolder
Get-ChildItem -Recurse -Exclude .git | foreach { git add $_.FullName }

# Commit the changes to the Git repository
git commit -m "Initial commit" | Out-Null
Start-Sleep -Seconds 1
git status

# Set the Git remote URL to the newly created GitHub repository
$gitRemoteUrl = "https://github.com/$githubUsername/$repoName.git"
Write-Output $gitRemoteUrl



