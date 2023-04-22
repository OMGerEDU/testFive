function Read-Configuration {
    $config = Get-Content "config.txt" -Raw | ConvertFrom-Json
    return $config
}

function Get-RepoRoot {
    return git rev-parse --show-toplevel
}

function Set-GitCredential ($repoRoot, $author, $repoName) {
    git -C $repoRoot remote remove origin
    git -C $repoRoot remote add origin "https://github.com/$author/$repoName.git"
}

function Fetch-RemoteBranch ($repoRoot, $branch) {
    git -C $repoRoot fetch origin $branch
}

function Checkout-Branch ($repoRoot, $branch) {
    $currentBranch = git -C $repoRoot rev-parse --abbrev-ref HEAD

    if ($currentBranch -ne $branch) {
        git -C $repoRoot checkout $branch
    }
}

function Merge-RemoteBranch ($repoRoot, $branch) {
    git -C $repoRoot merge origin/$branch
}

# Main script execution
$config = Read-Configuration
$author = $config.Author
$repoName = $config.repoName
$repoRoot = Get-RepoRoot

$branch = Read-Host "Enter the branch name to update from"

Set-GitCredential $repoRoot $author $repoName
Fetch-RemoteBranch $repoRoot $branch
Checkout-Branch $repoRoot $branch
Merge-RemoteBranch $repoRoot $branch

Write-Host "Successfully updated the branch $branch"
