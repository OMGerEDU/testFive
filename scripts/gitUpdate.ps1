    if ($args[0] -eq $null) {
        Write-Host ".\fileName branchName - branch name missing."
        return
    }

    if ($args[1] -eq $null) {
        Write-Host "Missing description."
        return
    }

    $env = Get-Content ".env" -Raw | ConvertFrom-Json

    # Set the variables from the configuration file
    $githubToken = $env.TOKEN
    # Define your GitHub repository information
    $githubUsername = $env.GITHUB_USERNAME

    # Read the configuration file
    $config = Get-Content "config.txt" -Raw | ConvertFrom-Json
    # Change the current working directory to the parent directory
    cd ..
    # Remove all existing remotes from the current Git repository
    $remotes = git remote
    foreach ($remote in $remotes) {
        git remote rm $remote
    }

    Write-Host $githubToken "Tokenoch"

    $repoName = $config.repoName
    # Set the Git remote URL to the newly created GitHub repository
    $gitRemoteUrl = "https://github.com/$githubUsername/$repoName.git"
    git remote add origin $gitRemoteUrl
    # Check if the branch exists
    $branchExists = git rev-parse --verify $args[0] !> $null
    if ($branchExists) {
        # Update the existing branch
        git checkout $args[0]
        git pull origin $args[0]
    } else {
        # Create and checkout a new branch
        git checkout -b $args[0]
    }

    # Add all changes to the Git staging area
    git add .

    # Commit the changes with a default commit message
    git commit -m $args[1]

    # Push the changes to the remote branch
    git push origin $args[0]

    # Output a success message
    Write-Host "Project updated and committed to branch $args[0] of $repoName"