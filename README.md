# cvs-tools

CVS Tools is Eclipse plugin which allows to keep records about commits into CVS. It could be used as overview of your work. You can search in your commit's history as in cvs plugin, but cvs search is focused only to resource, not to whole change set. The records are stored in local SQLite database. When a file or change set is commited, it's able to be send directly a comment contained the changes into JIRA task. When you use more than one workspace with same Eclipse installation, you are able to view changes from another workspace. This tool also allows to search in your changes through of all workspaces. 

This tool was developed only for my personal needs. I use it in my job every day.

When the plugin is run at first time, it will be created a new empty database in a path (eclipse_installation)/plugins/cvs.tools/cvstools.db
It will be also generated a config file in the same directory /config.properties

The allowed configuration is:

```properties
#Mon May 08 01:22:54 CEST 2017

#username to database
user=cvstools

#password to database
password=cvstools

#location of database
database=C:/Development/eclipse/plugins/cvs.tools/cvstools.db

#jira username
JIRA_username=

#jira password
JIRA_password=

#JIRA API REST url to insert a new comment
# the placeholder ${issue} will be replaced with issue number from a commit's comment
JIRA_addCommentRestUrl=http://JIRA_URL/rest/api/2/issue/${issue}/comment

#template of a JIRA comment 
# It's able to be used these placeolders:
# ${issue} - issue number
# ${version} - name of the current workspace
# ${resources} - list of resources in a change set
JIRA_commentTemplate=CHANGES in ${version}:\n${resources}

# direct link to a JIRA issue
JIRA_issueUrl=http://JIRA_URL/browse/${issue}

#pattern to resolve a JIRA issue number from a commit's comment
JIRA_issueNumberPattern = ^[A-Z]+-\d+

#if it's set, to the list of resources in JIRA comment is added before a filename type of operation I -inserted, U-updated, D-deleted
addOperationTypeToResource=true

#limit of number of returned changesets
changeSetsLimit=100000

```

![Main window](https://github.com/lukasgal/cvs-tools/blob/master/docs/images/mainWindow.png)

