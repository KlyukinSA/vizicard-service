curl -X POST "http://localhost:8081/users/me/avatar" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "file=@/home/stepan/sdfg.png;type=image/png" -H 'Authorization: Bearer '$1
#curl -X POST "http://localhost:8081/users/me/avatar" -H  "accept: */*" -H  "Content-Type: multipart/form-data" -F "file=@~/124285256.jpeg;type=image/jpeg" -H 'Authorization: Bearer '$1
