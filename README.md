# wdi-team9

## Input Folder Structure

This folder contains the individual source datasets.

The kaggle dataset was too large to be used during identity resolution. Therfore it was split into managable partitions.
The folder test contains these partitions. 


## Gold Standard folder
The gold standard folder contains the fold standard  used for evalutaion of the matching rules and for training of the classifiers.

For each dataset combination it contains:
- a ds1_ds2 csv file, which contains the named correspondences up to a similarity level of 0.5 for 3 different simple matching rules
- an _analysis.xlsx file which was used for manual labeling
- a _train.csv and _test.csv file containing the training and test set for the dataset combination
- a _cat file, which indicates to which category of the gs the correspondence belongs: sure positive/negative or corner case


## Output Folder Structure

### Folder: combinedFiles
These files contain the combined similarities from three different matching rules.
They are the basis for the gold standard creation

### Folder: experiments
These files describe the output of an experiment.
An Experiment is identified by an indentifier {matching_rule_id} _ {blocker_id} _ {matching threshold}.
Each folder contains correspondeces, block size information, and gold standard evaluation results.

The file experiment_log.csv contains a combined overview log of the main information generated by each experiment.


### Folder: gs
This folder contains the individual IR files per matching rule that are then combined in the "combinedFiles" folder.

### Folder: namedgs
Sames as gs, but additionally with company names for easy manual evaluation.

### Folder: reducedKaggleSet
The kaggle dataset was too large to be processed by one machine. Therefore a simple similarity function was run across the whole file.
Any company name that had at least 30% similarity with one of the company names in dbpedia, forbes, or dw was kept, the other ones discarded.
This folder contains the output of the similarity comparison as well as the reduced xml files per partition.