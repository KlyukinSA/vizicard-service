jwt=`bash signup.sh $1`
bash see_profile.sh $jwt
bash update_profile.sh $jwt
bash see_profile.sh $jwt
