```
http :8080/party event=create payload:={"partyKey": "12", "name":"sss"}
```

```
curl -v -H "Content-Type: application/json" -d '{"event": "create", "payload": {"partyKey": "12", "name":"sss"}}' localhost:8080/party
curl -v -H "Content-Type: application/json" -d '{"event": "create", "payload": {"roleKey": "12", "type":1}}' localhost:8080/role
```
