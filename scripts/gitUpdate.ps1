function Read-Configuration {
    $config = Get-Content "config.txt" -Raw | ConvertFrom-Json
    $env = Get-Content ".env" -Raw | ConvertFrom-Json
    return $config, $env
}

function Get-RepoRoot {
    return git rev-parse --show-toplevel
}

function SetUp-RemoteRepository ($repoRoot, $author, $repoName, $username, $token) {
    git -C $repoRoot remote remove origin
    git -C $repoRoot remote add origin "https://${username}:${token}@github.com/$author/$repoName.git"
}

function CheckAndSwitch-Branch ($repoRoot, $branch) {
    $branchExists = git -C $repoRoot ls-remote --exit-code --heads origin $branch

    if (!$branchExists) {
        git -C $repoRoot checkout -b $branch
    } else {
        git -C $repoRoot checkout $branch
    }
}

function StageAndCommit-Changes ($repoRoot, $commitMessage) {
    git -C $repoRoot add -A

    $changes = git -C $repoRoot status --porcelain

    if (!$changes) {
        Write-Host "No changes detected in the working directory. Skipping the commit."
    } else {
        git -C $repoRoot commit -m $commitMessage
    }
}

function FetchAndMerge-Changes ($repoRoot, $branch) {
    git -C $repoRoot fetch origin $branch
    $conflicts = git -C $repoRoot diff --name-only --diff-filter=U

    if ($conflicts) {
        Write-Host "There are merge conflicts. Please resolve them manually and try again."
        exit 1
    }

    git -C $repoRoot merge origin/$branch
}

function Push-Changes ($repoRoot, $branch) {
    git -C $repoRoot push origin $branch
}

# Main script execution
$config, $env = Read-Configuration
$author = $config.Author
$repoName = $config.repoName
$username = $env.GITHUB_USERNAME
$token = $env.TOKEN
$repoRoot = Get-RepoRoot

SetUp-RemoteRepository $repoRoot $author $repoName $username $token

$branch = Read-Host "Enter the branch name"
$commitMessage = Read-Host "Enter the commit description"

CheckAndSwitch-Branch $repoRoot $branch
StageAndCommit-Changes $repoRoot $commitMessage
FetchAndMerge-Changes $repoRoot $branch
Push-Changes $repoRoot $branch

Write-Host "Project updated and committed to branch $branch"
