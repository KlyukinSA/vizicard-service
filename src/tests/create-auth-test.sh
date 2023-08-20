# get base of request in swagger, run `bash <this-script> <new-test-name>`
xclip -o > $1 && echo " -H 'Authorization: Bearer '\$1" >> $1
