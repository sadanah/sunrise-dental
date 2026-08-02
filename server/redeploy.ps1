# redeploy.ps1 — run from sdc-new/server/
Write-Host "Building..."
mvn clean package

$warSource = "target\sunrise-dental-clinic.war"
$tomcatWebapps = "D:\Apps\Tomcat Server\apache-tomcat-9.0.120\webapps"

if (-not (Test-Path $warSource)) {
    Write-Host "Build failed or WAR not found - aborting deploy."
    exit 1
}

Write-Host "Clearing old deployment..."
Remove-Item "$tomcatWebapps\sunrise-dental-clinic" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$tomcatWebapps\sunrise-dental-clinic.war" -Force -ErrorAction SilentlyContinue

Write-Host "Copying new WAR..."
Copy-Item $warSource $tomcatWebapps

Write-Host "Done. Start Tomcat manually with startup.bat, then visit http://localhost:8080/sunrise-dental-clinic/api/login"