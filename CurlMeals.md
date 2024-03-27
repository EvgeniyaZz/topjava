# getAll
curl http://localhost:8080/topjava/rest/meals/

# get
curl "http://localhost:8080/topjava/rest/meals/100009"

# get filtered
curl "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&endDate=2020-01-30&startTime=10%3A00&endTime=14%3A00"

# update
curl -X PUT -H "Content-Type: application/json; charset=Windows-1251" -d '{"dateTime":"2020-01-31T20:00","description":"Еда","calories":"1000"}' http://localhost:8080/topjava/rest/meals/100009

# delete
curl -X DELETE "http://localhost:8080/topjava/rest/meals/100009"

# create
curl -X POST -H "Content-Type: application/json; charset=Windows-1251" -d '{"id":"","dateTime":"2024-03-27T19:00","description":"Ужин","calories":"850"}' http://localhost:8080/topjava/rest/meals/