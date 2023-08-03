curl -X GET http://localhost:8080/users/me -H 'Authorization: Bearer '$(curl -X POST 'http://localhost:8080/users/signin?username=client&password=client')  -H 'Authorization: Bearer '$1
echo
