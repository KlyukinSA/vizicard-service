# get base of request in swagger, run `bash add_auth_test.sh <new-script-name>`
xclip -o > $1 && echo " -H 'Authorization: Bearer '\$1" >> $1
