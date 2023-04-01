# Read the configuration file
$config = Get-Content "config.txt" -Raw | ConvertFrom-Json
$env = Get-Content ".env" -Raw | ConvertFrom-Json

# Define your GitHub repository information
$username = $env.GITHUB_USERNAME
$repoName = $config.repoName


# Find the root directory of the repository
$repoRoot = git rev-parse --show-toplevel

# Get the branch, commit message, username, and token
$branch = Read-Host "Enter the branch name"
$commitMessage = Read-Host "Enter the commit description"

# Set the Git credential for the repository
git -C $repoRoot remote set-url  "https://github.com/$username/$repoName.git"

# Ensure that we are on the correct branch
git -C $repoRoot checkout $branch

# Check if there are any changes in the working directory
$changes = git -C $repoRoot status --porcelain

if (!$changes) {
    Write-Host "No changes detected in the working directory. Skipping the commit."
} else {
    # Add all changes to the Git staging area
    git -C $repoRoot add .

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
