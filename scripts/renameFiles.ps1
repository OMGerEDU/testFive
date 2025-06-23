param(
    [string]$Folder = "."
)

$i = 1
Get-ChildItem -Path $Folder -File | Sort-Object Name | ForEach-Object {
    $ext = $_.Extension
    $newName = "{0}{1}" -f $i, $ext
    Rename-Item -Path $_.FullName -NewName $newName
    $i++
}
