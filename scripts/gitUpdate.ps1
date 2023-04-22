# Read the configuration file
$config = Get-Content "config.txt" -Raw | ConvertFrom-Json
$env = Get-Content ".env" -Raw | ConvertFrom-Json

# Define your GitHub repository information.
$author = $config.Author
$repoName = $config.repoName

# Define your GitHub credentials from the .env file
$username = $env.GITHUB_USERNAME
$token = $env.TOKEN

# Find the root directory of the repository
$repoRoot = git rev-parse --show-toplevel
Write-Host "https://github.com/$author/$repoName.git"

# Set up Git credentials and add the remote repository
git -C $repoRoot remote remove origin # Remove the existing remote (if any) to avoid conflicts
git -C $repoRoot remote add origin "https://${username}:${token}@github.com/$author/$repoName.git"


$branch = Read-Host "Enter the branch name"
$commitMessage = Read-Host "Enter the commit description"
# Check if the branch exists remotely
$branchExists = git -C $repoRoot ls-remote --exit-code --heads origin $branch

if (!$branchExists) {
    # If the branch doesn't exist, create and checkout a new branch
    git -C $repoRoot checkout -b $branch
} else {
    # If the branch exists, checkout the branch
    git -C $repoRoot checkout $branch
}

# Add all changes to the Git staging area
git -C $repoRoot add -A

# Check if there are any changes in the working directory
$changes = git -C $repoRoot status --porcelain

if (!$changes) {
    Write-Host "No changes detected in the working directory. Skipping the commit."
} else {
    # Commit the changes
    git -C $repoRoot commit -m $commitMessage
}

# Fetch the latest changes from the remote branch
git -C $repoRoot fetch origin $branch

# Check if there are any conflicts between the local and remote branches
$conflicts = git -C $repoRoot diff --name-only --diff-filter=U

if ($conflicts) {
    Write-Host "There are merge conflicts. Please resolve them manually and try again."
    exit 1
}

# Merge the latest changes from the remote branch
git -C $repoRoot merge origin/$branch

# Push the changes to the remote branch
git -C $repoRoot push origin $branch

# Output a success message
Write-Host "Project updated and committed to branch $branch"
