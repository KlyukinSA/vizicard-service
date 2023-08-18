curl -X PUT "http://localhost:8081/users/me" -H  "accept: */*" -H  "Content-Type: application/json" -d "{  \"name\": \"string\",  \"title\": \"string\",  \"description\": \"string\",  \"city\": \"string\",  \"contacts\": [    {      \"contact\": \"string\",      \"type\": \"PHONE\"    }  ]}" -H 'Authorization: Bearer '$1
echo
