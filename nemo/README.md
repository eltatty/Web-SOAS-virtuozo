docker build . -t nemo
docker run --name nemo -p 8085:8085 nemo