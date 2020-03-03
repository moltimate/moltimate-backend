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
