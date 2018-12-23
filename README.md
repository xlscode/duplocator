# DupLocator

DupLocator is a program to locate duplicate files on the disk with some extra options.
It is being written to practice java as well as to "scratch own itch".

##### It reports:
* duplicates
* the directories where the duplicated files reside
* files with the same name (grouped according to content)
* files and directories that the program was unable to read (i.e. because of file permissions)

##### Planed features
* reporting number of files processed in time (and total files processed)
* command line options
* reporting space on disk wasted due to duplication of files   
* multiple directories to search 
* filters for directory and file names to be included in (or excluded from) search
* gui version


##### Programming techniques used
As this program is a training project, this is a list of elements, patterns and techniques used:
* inner classes
* iterators
* generic classes
* abstract class (one)
* dependency injection (no, no containers)

#### License
GNU General Public License v3.0
