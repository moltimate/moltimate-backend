# moltimate-backend
ProMol successor's backend application.

### Contents

* [Setup & Run](#setup-run)
* [Wiping Local Database](#wiping-local-database)
* [API Summary](#api-summary)
* [API Details](#api-details)


<a name="setup-run"></a>
### Setup & Run

#### IntelliJ

To start the application from IntelliJ, import the project as a Maven project, and run the `main(...)` entrypoint in [`Application.java`](`src/main/java/com/moltimate/moltimatebackend/Application.java`).

#### Maven CLI

To start the application using Maven's CLI, run:

        mvn spring-boot:run

<a name="wiping-local-database"></a>
### Wiping Local Database

To wipe the database, delete the generated file `moltimate.mv.db` at the root of the project.

<a name="api-summary"></a>
### API Summary

This briefly summarizes all API endpoints.

#### Alignments

| HTTP Method | Endpoint | Function |
|:------------|:---------|:---------|
| POST | [/align/activesite](#post-align-activesite) | Aligns protein active sites with motifs |

#### Motifs

| HTTP Method | Endpoint | Function |
|:------------|:---------|:---------|
| GET | [/motifs](#get-motifs) | Get a list of motifs |
| GET | [/motifs/{pdbid}](#get-motifs-pdbid) | Get a specific motif |
| GET | [/motifs/debug](#get-motifs-debug) | TEMPORARY. Stores 4 hard-coded Motifs into the database |

#### Ligands

| HTTP Method | Endpoint | Function |
|:------------|:---------|:---------|
| GET | [/ligands/{ECNumber}](#get-ligands-ec) | Get ligands associated with the specified EC |

#### Docking

| HTTP Method | Endpoint | Function |
|:------------|:---------|:---------|
| POST | [/dock/dockligand](#dock-ligand) | Begins AutoDock Vina job |
| GET | [/dock/dockligand](#get-docked-ligand) | Retrieves binding affinities and active sites, and starts final OpenBabel conversion |
| GET | [/dock/retrievefile](#retrieve-file) | Retrieves OpenBabel job |
| GET | [/dock/exportLigands](#export-ligands) | Exports ligand data to csv format |

<a name="api-details"></a>
### API Details

This outlines every API's endpoints, request types, and expected request parameters or JSON payload.

#### Alignments

<a name="post-align-activesite"></a>
##### POST /align/activesite
###### Aligns protein active sites with motifs

Request body parameters

| Parameter | Type | Function |
|:----------|:-----|:---------|
| pdbIds | String Array | The PDB IDs of the proteins that will be aligned with each motif |
| ecNumber | String | Filters the set of motifs. This can be partially-qualified ("3", ""3.4", "3.4.21", or "3.4.21.1") |
| options | String Array | NOT IMPLEMENTED. Additional options available for active site alignments |
| filters | String Array | NOT IMPLEMENTED. Additional ways to filter the set of motifs |


Example JSON request
```json
{
    "pdbIds": ["8gch", "1ezi", "1ma0"],
	"ecNumber": "3.4.21.1",
	"options": ["rmsd"],
	"filters": []
}
```




#### Motifs

<a name="get-motifs"></a>
##### GET /motifs
###### Get a list of motifs

Query string parameters

| Parameter | Type | Function |
|:----------|:-----|:---------|
| ecnumber | String | An optional EC number to filter the list of motifs by |

<a name="get-motifs-pdbid"></a>
##### GET /motifs/{pdbid}
###### Get a specific motif

Path parameters

| Parameter | Type | Function |
|:----------|:-----|:---------|
| pdbid | String | The unique PDB ID of the motif |

<a name="get-motifs-debug"></a>
##### GET /motifs/debug
###### TEMPORARY. Stores 4 hard-coded Motifs into the database


#### Ligands

<a name="get-ligands-ec"></a>
##### GET /ligands/{ECNumber}
###### Get ligands associated with the specified EC

Path parameters

| Parameter | Type | Function |
|:----------|:-----|:---------|
| ECNumber | String | The EC number you want ligands associated with |


#### Docking

###### To perform docking, start by calling the ```POST /dock/dockligand``` endpoint.
###### Once you receive a 200 status and a job ID, continue to query the ```GET /dock/dockligand``` endpoint
###### Once you receive a 200 status, active site data, binding affinities and job ID, call ```GET /dock/retrievefile```
###### Continue to call ```GET /dock/retrievefile``` until a 200 status is returned.
###### If you would like to receive the completed docked file again, you may call the ```GET /dock/retrievefile``` endpoint
###### The location of the AutoDock Vina and OpenBabel deployments may be configured in the ```application.properties``` file

#### Docking API

<a name="dock-ligand"></a>
##### POST /dock/dockligand
###### Convert the supplied ligand and protein to pdbqt format using OpenBabel then start an AutoDock Vina job

##### Form Parameters

| Parameter | Type | Function |
|:----------|:-----|:---------|
| macromolecule | File | Protein file for docking |
| ligand | File | Ligand file for docking |
| ligandID | String | Optional ligand ID. If ligand file is not provided, ligand ID will be used to download ligand from the PDB |
| macromoleculeID | String | Optional macromolecule ID. If macromolecule file is not provided, macromolecule ID will be used to download macromolecule from the PDB |
| center_x | number | Center of docking area in the x axis |
| center_y | number | Center of docking area in the y axis |
| center_z | number | Center of docking area in the z axis |
| size_x | number | Size of docking area in the x axis |
| size_y | number | Size of docking area in the y axis |
| size_z | number | Size of docking area in the z axis |

##### Returns

###### 200 OK - When job has been completed successfully

| Parameter | Type | Function |
|:----------|:-----|:---------|
| jobId | String | ID of the AutoDock Vina job |
| macromolecule | String | Name of macromolecule file |
| ligand | String | Name of ligand file |

<a name="get-docked-ligand"></a>
##### GET /dock/dockligand
###### Checks the status of an AutoDock Vina job. When complete, sends docked ligand file to OpenBabel and returns a list of binding affinities and active sites.
###### For active site generation to function, ensure that the motif database has been generated.

##### Path Parameters

| Parameter | Type | Function |
|:----------|:-----|:---------|
| storage_hash | String | ID of the AutoDock Vina job |
| pdbId | String | Id of protein for determination of active sites |

##### Returns

###### 200 OK - When job has completed successfully
###### 203 - When job is still processing

| Parameter | Type | Function |
|:----------|:-----|:---------|
| babelJobId | String | Job ID of OpenBabel file conversion |
| dockingData | array | List of binding affinities and free energy values |
| activeSites | array | List of active site data for protein |

<a name="retrieve-file"></a>
##### GET /dock/retrievefile
###### Retrieves a completed job from OpenBabel

##### Path Parameters
| Parameter | Type | Function |
|:----------|:-----|:---------|
| storage_hash | String | ID of the OpenBabel job |

##### Returns
###### 200 OK - When job has completed successfully
###### 203 - When job is still processing

###### Requested completed pdb file

<a name="export-ligands"></a>
##### POST /dock/exportLigands
###### Converts the provided array of free energy values into a csv format

##### JSON Parameters
| Parameter | Type | Function |
|:----------|:-----|:---------|
| ligands | array | List of ligands to export |

###### Ligand
| Parameter | Type | Function |
|:----------|:-----|:---------|
| name | String | name of ligand |
| bindingEnergy | number | Binding energy of ligand |
| modeNumber | number | Mode number of ligand |
| rmsdUpper | number | Upper bound of rmsd of ligand |
| rmsdLower | number | Lower bound of rmsd of ligand |

##### Returns

###### 200 OK
###### csv output file in the format Name,Mode Number,Binding Energy,RMSD Lower,RMSD Upper
