docker build -t audio-storage .



curl --request POST \
  --url http://localhost:8080/users \
  --header 'Content-Type: application/json' \
  --data '{
        "userName": "Test User"
}'

curl --request POST \
  --url http://localhost:8080/phrases \
  --header 'Content-Type: application/json' \
  --data '{
        "phraseText": "Hello, world!"
}'

curl --request POST 'http://localhost:8080/audio/user/1/phrase/1' --form 'audio_file=@./sample3.m4a'

curl --request GET 'http://localhost:8080/audio/user/1/phrase/1/m4a' -o './test_response_file_1_1.m4a'