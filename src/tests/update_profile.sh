curl -X PUT "http://localhost:8081/users/me" -H  "accept: */*" -H  "Content-Type: application/json" -d "{  \"contacts\": [    {      \"contact\": \"email2\",      \"type\": \"MAIL\"    }  ]}" -H 'Authorization: Bearer '$1
echo
