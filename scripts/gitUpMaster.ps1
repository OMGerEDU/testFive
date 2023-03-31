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

$repoName = $config.repoName
# Set the Git remote URL to the newly created GitHub repository
$gitRemoteUrl = "https://github.com/$githubUsername/$repoName.git"
git remote add origin $gitRemoteUrl
# Push the local Git repository to the remote GitHub repository
git push -u origin master