# Read the configuration file
$config = Get-Content "config.txt" -Raw | ConvertFrom-Json


if ($args[0] -eq $null) {
    Write-Host "No collab name entered"
    return
}
$collaboratorUsername = $args[0]


# Set the variables from the configuration file
$githubToken = $env.TOKEN
# Define your GitHub repository information
$githubUsername = $env.GITHUB_USERNAME

# Define the API endpoint URI for adding a collaborator
$collaboratorUri = "https://api.github.com/repos/$githubUsername/$repoName/collaborators/$collaboratorUsername"

# Set the headers for the API request
$headers = @ {
    Authorization = "Token $githubToken"
    Accept = "application/vnd.github.v3+json"
}
# Make the API request to add the collaborator
try {
    Invoke-RestMethod -Method Put -Uri $collaboratorUri -Headers $headers
    Write-Host "Successfully added $collaboratorUsername as a collaborator to $repoName"
} catch {
    Write-Host "Failed to add $collaboratorUsername as a collaborator to $repoName. Error message: $($_.Exception.Message)"
}