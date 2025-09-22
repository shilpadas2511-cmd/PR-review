# CommerceCore

This repository represents the core extensions to the SAP Commerce platform.  Its structure will be defined according to SAP's guidelines:
to<https://help.sap.com/docs/SAP_COMMERCE_CLOUD_PUBLIC_CLOUD/1be46286b36a4aa48205be5a96240672/36cc68d35f784dad9f70afeda5ef6239.html>

## Development Environment
### Local Hybris set up steps for 2211 version
Local installation link: <https://help.sap.com/docs/SAP_COMMERCE/a74589c3a81a4a95bf51d87258c0ab15/4e00fd71f0dd4dcab248f022a8c98d3d.html?q=local%20installation#loio4e00fd71f0dd4dcab248f022a8c98d3d>
List of software required:
1. 7-zip
2. Java 17 version: <https://developer.ibm.com/languages/java/semeru-runtimes/downloads/>
3. Git.exe: <https://git-scm.com/downloads>

### Hybris Set-up
1. Download Hybris .7z file from below box link: <https://ibm.ent.box.com/folder/281938815903>
  Create a folder ‘Hybris’ and unzip the contents of CXCOMCL221100U_27-70007431 into it.
  Unzip the content of CXCOMIEP221100U_24-70007891 and copy and merge the content into hybris\bin\modules
2. Set `JAVA_HOME` Path and javac Path correctly. <https://www.geeksforgeeks.org/how-to-set-java-path-in-windows-and-linux/>
3. Create a new folder named `PartnerQuote` and clone the Partner Quoting Commerce codebase from URL - <https://github.ibm.com/DSWC-SAP-Commerce/CommerceCore.git>
  Checkout to branch feature/main
4. Soft Link the git project directory to the hybris suite using command
  Note: Please make sure there is no folder named `custom` inside `path/to/Hybris/hybris/bin/`, If present delete first and then proceed with below command to prevent folder already existis error.
  `mklink /j path/to/Hybris/hybris/bin/custom path/to/PartnerQuote/core-customize/hybris/bin/custom`
5. Soft Link the config
  "mklink /j path/to/Hybris/hybris/config_REPO    path/to/PartnerQuote/core-customize/hybris/config"
  Copy all the contents of config directory and paste them to path/to/Hybris/hybris/config_REPO folder (Replace all the existing files).
  Remove the previous config directory and rename the config_REPO to config.
  Or
  Copy the config folder from path/to/PartnerQuote/core-customize/hybris to path/to/Hybris/hybris
6. Open command prompt and move to path  path/to/Hybris/hybris/bin/platform. And run the following commands.
   setantenv.bat
   ant clean all
7. Run the following command-
   hybrisserver.bat
8. Open a browser and hit the URL <https://localhost:9002> and check if HAC is loading.The screen will prompt to Initialize ,Click on Initialize.
9. After successful initialization, check if Backoffice and hac is loading properly.  Backoffice Url: <https://localhost:9002/backoffice/login.zul>

### Setting up the IntelliJ IDE
1. Open IntelliJ settings>Plugins>Search. Search for “SAP Commerce Developer Toolsets” and install it.
2. Then open the file>open>path/to/Hybris>hybris/bin directory and open it. Opening the hybris directory in IntelliJ will set the project type as Hybris

### Setting up the Eclipse IDE
1. Open Eclipse,Click on File->Import.
2. In 'Select an Import wizard' screen,Click on 'General' and select 'Existing Projects into Workspace'.
3. In the Import Projects screen of the Import wizard, click on the 'Browse' button for the Select root directory field.
4. Navigate to the Hybris directory into which you have unzipped the SAP Commerce files. Select the hybris directory and click the Open   button.
5. In the Import Projects screen of the Import wizard, we can see all the extensions showing up,check or clear the projects Eclipse has   located to include or exclude the projects you need, then click the Finish button.Now we can see all the extensions in eclipse active   workspace.

### Setup Detect Secrets
It is expected that every developer setup and run detect secrets on code before delivering.  This ensures secrets are not delivered to code base which breaks ITSS compliance.  Information on this compliance step and how to install detect secrets locally is here:
<https://w3.ibm.com/w3publisher/detect-secrets>
Once you have detect secrets installed the following steps should be run before pushing a commit to GitHub.
- Scan the local source for secrets: `detect-secrets scan --update .secrets.baseline`
- Audit the found secrets: `detect-secrets audit .secrets.baseline`
  - The development must resolve ALL actual secrets by removing them from code, anything else found that is not actually a secret can be answer with "no (n)" that the value is not a secret, which will be stored in the `.secrets.baseline` file so it does not have to be audited again.
  - If detect-secrets finds new values that have not been removed and are not actual secrets, the team will need to add the `.secrets.baseline` file to their code commit.
  
## CI/CD Process
The CI/CD process is intended to be delivered through a true continuous delivery process.  That process is documentated in the linked [Mural Diagram](https://app.mural.co/t/dswecosystem1400/m/dswweb4973/1698944274666/d567274456afd713586a2a37f2bf54731e10544c?sender=bahepbur6206).
All Commerce Core repo builds and deploys are done through tekton pipelines which are kicked off in IBM Cloud toolchain.  When a pull request is created or updated (new commit added) the SAP builds are triggered to validate the quality of the code.  When the code is merged into the main branch, both a SAP build and a SAP deployment utilizing API calls to SAP Commerce.
Important links of this CI/CD setup:
- IBM Cloud Toolchain that is invoked: <https://cloud.ibm.com/devops/pipelines/tekton/c0ad2bac-891e-4c38-9164-6e6c76d65a43?env_id=ibm:yp:us-south>
- Pipeline Definition Repo: <https://github.ibm.com/DSWC-SAP-Commerce/PlatformPipeline>
- Platform repo for defining the IBM Cloud pipeline resources: <https://github.ibm.com/DSWC-SAP-Commerce/PlatformInfrastructure>
Three pipelines exist:
- **dswc-sap-commerce-commercecore-pull-request** - Any commits pushed to a feature branch that has a pull request open against main branch
- **dswc-sap-commerce-commercecore-main** - pull request merged to main branch
- **dswc-sap-commerce-commercecore-pull-request-closed** - When pull request is closed, any builds associated with that PR are deleted from the SAP Cloud.*

### Deployment Customization
As stated above, by default a deployment is triggered on every delivery to main with the database mode is set to `NONE` in the call to SAP Commerce.  The pipeline does allow deployment to be skipped or the database mode to be changed to the [database migration mode defined by SAP Commerce](https://help.sap.com/docs/SAP_COMMERCE_CLOUD_PUBLIC_CLOUD/0fa6bcf4736c46f78c248512391eb467/106f7a8370db44a0b052f4a0cd5c4deb.html).  Those customizations are done via the [pull request label process](https://docs.github.com/en/issues/using-labels-and-milestones-to-track-work/managing-labels#applying-a-label).

#### Skip Deployment
To skip deployment, add label `pipeline: no deploy` in your pull request under **Labels** section.

#### Invoke Database Migration
To change the database mode from _No migration required_ to _Migrate data_, add label `pipeline: db migration` in your pull request under **Labels** section.
**Note:** at this time, the pipeline does not support database initialization.  That will need done manually by skipping the deploy and then triggering deploy manually.
