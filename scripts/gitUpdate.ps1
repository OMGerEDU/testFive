# Define the branch and commit message
$branch = $args[0]
$commitMessage = $args[1]

# Ensure that we are on the correct branch
git checkout $branch

# Add all changes to the Git staging area
git add .

# Commit the changes
git commit -m $commitMessage

# Fetch the latest changes from the remote branch
git fetch origin $branch

# Check if there are any conflicts between the local and remote branches
$conflicts = git diff --name-only --diff-filter=U

if ($conflicts) {
    Write-Host "There are merge conflicts. Please resolve them manually and try again."
    exit 1
}

# Merge the latest changes from the remote branch
git merge origin/$branch

# Push the changes to the remote branch
git push origin $branch

# Output a success message
Write-Host "Project updated and committed to branch $branch"
