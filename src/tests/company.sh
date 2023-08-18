curl -X PUT "http://localhost:8081/users/me/company" -H  "accept: */*" -H  "Content-Type: application/json" -d "{  \"name\": \"string2\",  \"title\": \"string2\",  \"description\": \"string\",  \"city\": \"string\",  \"contacts\": [    {      \"contact\": \"string\",      \"type\": \"PHONE\"    }  ]}"   -H 'Authorization: Bearer '$1
echo
