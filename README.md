Sustainable Resource Planning
=============================

Getting started
---------------

The easiest way to get started is to build and run a docker container. This
will, by default, use an embedded h2 database but you will need to connect
a keycloak identity server.

Build image:

```
./mvnw install
```

Run image, using just HTTP Basic security with a username and password provided.
This is handy for local development and testing.

```
docker run -p 8080:8080 \
  -e SPRING_SECURITY_USER_NAME=<usr> \
  -e SPRING_SECURITY_USER_password=<pwd> \
  knowprocess/srp
```

Useful commands
---------------

- Check health of a particular return
  
 ```
 curl -v -u usr:pwd http://localhost:8080/admin/health/SDU-2020-21/ZZ1
 ```

- Import previous year's return into this years

 ```
 curl -v -X POST -u usr:pwd http://localhost:8080/returns/import/SDU-2019-20-ZZ1/SDU-2020-21-ZZ1
 ```

- Create / update a survey with the latest definition

 ```
 curl -v -X POST -u usr:pwd http://localhost:8080/admin/data-mgmt/surveys/SDU-2020-21
 ```

Configuring security using OAuth2 via Keycloak
----------------------------------------------

This assumes you have a Keycloak server available, there are any resources on
how to provide this including: https://www.keycloak.org/docs/latest/.

The image may then be started as follows:

```
docker run -p 8080:8080 \
  -e KEYCLOAK_REALM=<realm> \
  -e KEYCLOAK_AUTH-SERVER-URL=<URL>/auth \
  -e KEYCLOAK_RESOURCE=<client_id> \
  -e KEYCLOAK_ENABLED=true \
  knowprocess/srp
```

Release History
---------------

2.4.2 - 17 Mar 21 - 8a4215a34876a2946a61dedc5be6aa9432ca8ba0

  - couple of dependabot upgrades
  - integrate jacoco code quality analysis

2.4.1 - 19 Nov 20 - e9e4cbb4a94fc92566ebffeca8bfc6a91c7799df

  - fix eclass and labels

2.4.0 - 02 Nov 20 (belatedly build release for 2019-20)

  - #10 Paper spend is referred to on the report as Spend Q5 but not captured
      in the return at all (bug)
  - #7 Minor cosmetic changes
  - #5 Email should make clear it's from an unmonitored mailbox
  - #4 Provide data exploration facility
  - #3 Add air pollution and plastics questions

2.3.0 - 23 Oct 18

  - #288 Implement 'health-check' for returns
  - #286 Render tables and graphs client side
  - #284 Remove 'Import ERIC data' (now implicit)

2.2.0 - 02 Aug 18 - 3515b3c38eed136e3a5663340489bdc31041f786

  - cosmetic changes and bug fixes

2.1.0 - 15 Mar 18

  - Revised benchmarking in reports

2.0.0 - 28 Feb 18

  - SDU reporting project

1.1.0 - 27 Jul 16

  - SDU Healthy Returns 2020 (macc project)

1.0.0 - 30 Sept 15

  - support the 50K app, included modules: disclosure, financial, contacts, workflow, decisions
