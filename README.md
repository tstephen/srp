Sustainable Resource Planning
=============================

Useful commands
---------------

- Check health of a particular return
  ```
  curl -v -u usr:pwd https://api.srp.digital/admin/health/SDU-2020-21/ZZ1
  ```
- Import previous year's return into this years
  ```
  curl -v -X POST -u usr:pwd https://api.srp.digital/returns/import/SDU-2019-20-ZZ1/SDU-2020-21-ZZ1
  ```
- Create / update a survey with the latest definition
  ```
  curl -v -X POST -u usr:pwd https://api.srp.digital/admin/data-mgmt/surveys/SDU-2020-21
  ```

Release History
---------------

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
