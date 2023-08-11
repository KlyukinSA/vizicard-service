curl -X POST "http://localhost:8081/api/users/me/avatar" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "file=@avatar.sh;type=text/plain" -H 'Authorization: Bearer '$1
