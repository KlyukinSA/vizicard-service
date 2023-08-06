curl -X POST "http://localhost:8080/users/me/avatar" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "file=@login.sh;type=text/plain" -H 'Authorization: Bearer '$1

