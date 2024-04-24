How to run Retype
==========================
Retype can be started by running the following command:
```shell
npm install retypeapp --global
retype start
```

1. Retype lists your documentation based on the folder structure
2. You can run Retype locally to see your documentation
3. When changes are merged onto the main branch, the documentation will be automatically updated
   1. We are using Github Actions to deploy to Github Pages
   2. A separate branch called `retype` is created to host the documentation
   3. The documentation is available for public: https://simonsays-pm4.github.io/simon-says/

---
Further documentation to retype can be found [here](https://retype.com/).
