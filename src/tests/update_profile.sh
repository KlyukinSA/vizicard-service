curl -X PUT "http://localhost:8080/users/me" -H  "accept: */*" -H  "Content-Type: application/json" -d "{  \"name\": \"string\",  \"position\": \"string\",  \"description\": \"string\",  \"company\": \"string\",  \"city\": \"string\",  \"contacts\": [    {      \"contact\": \"string\",      \"contactEnum\": \"PHONE\"    }  ]}"  -H 'Authorization: Bearer '$1
echo
curl -X PUT "http://localhost:8080/users/me" -H  "accept: */*" -H  "Content-Type: application/json" -d "{  \"contacts\": [    {      \"contact\": \"string2\",      \"contactEnum\": \"PHONE\"    }  ]}" -H 'Authorization: Bearer '$1
echo
curl -X PUT "http://localhost:8080/users/me" -H  "accept: */*" -H  "Content-Type: application/json" -d "{  \"contacts\": [    {      \"contact\": \"string3\",      \"contactEnum\": \"PHONE\"    },    {      \"contact\": \"string4\",      \"contactEnum\": \"PHONE\"    }  ]}" -H 'Authorization: Bearer '$1
echo