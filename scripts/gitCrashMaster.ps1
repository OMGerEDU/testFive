# Read the configuration file
$config = Get-Content "config.txt" -Raw | ConvertFrom-Json
$env = Get-Content ".env" -Raw | ConvertFrom-Json
# Change the current working directory to the parent directory
cd ..

# Remove all existing remotes from the current Git repository
$remotes = git remote
foreach ($remote in $remotes) {
    git remote rm $remote
}


$repoName = $config.repoName
# Set the variables from the configuration file
$githubToken = $env.TOKEN
# Define your GitHub repository information
$githubUsername = $env.GITHUB_USERNAME

# Set the Git remote URL to the newly created GitHub repository
$gitRemoteUrl = "https://github.com/$githubUsername/$repoName.git"
git remote add origin $gitRemoteUrl

# Add all changes to the staging area
git rm -r --cached target

git add .

# Create a new commit with a message
git commit -m "Update"

# Remove untracked files and directories
git clean -df

# Push the local Git repository to the remote GitHub repository
git push -u origin master