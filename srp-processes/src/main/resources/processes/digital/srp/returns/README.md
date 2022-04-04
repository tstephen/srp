README for srp.digital/returns
==============================


1. Submit annual returns when end of year reached

   ```
    ./kp.py -v -u tim@knowprocess.com:Veloceraptor -X POST -d 'json={ "ids": [ 1478 ] }' https://api.srp.digital/msg/sdu/srp.returns.json
   ```