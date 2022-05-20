# Flightright Group challenge #1

You will find in this file both: the description of the solution, and the description of the task (I left it under the
solution description)

## Solution

### Thoughts and description

The main point here is that the amount of data can be too large, meaning RAM is not enough to store all the data
necessary for the count of requested statistics. Indeed, in order to count unique users per source in Java, we need to
store all entries in RAM, which can easily exceeded (on my machine one such objects takes up to 200 bytes).

To solve this problem, we can use something like aside DB to store save all the data, and then aggregate the requested
statistics.

### Details

- The application is done on SpringBoot. The main class is called **FileProcessor** and located in the root package of
  the project.
- The data is handled with batches of 100000 lines (which can be changed). For this I use ThreadPoolExecutor. The
  Executor has limited queue of 20 tasks and 10 active threads, which in total limits the amount of inmemory objects up
  to (20 + 10) * 100000 = 3000000 items. If every object takes 200 bytes, they will use no more than 600 MB
  approximately. This approach allows to load the lines from file gradually and therefore to avoid out of memory errors.
- After 100000 lines were read from the file, they are stored into a repository. Then, when everything stored, the data
  is aggregated on the repo side and written into an output file. I implemented three different repos.
    - The first is the class **UserEntryRepoMock** which has under the hood a Map from source to the set of users from
      this source.
    - The second is the class **UserEntryRepo** which stores the data into Postgre sql (for example). This approach is
      very slow. 100000 rows are written for 5 seconds on my machine approximately. It can be improved by using COPY
      command of PostgreSQL, instead of java tools.
    - The third is **UserEntryRepoHashMock**. This is also inmemory repo, but it is a very memory efficient one. It contains
      under the hood a map from source to the set of integer hashes of the users (*not users, but their hashes*). This
      set is a memory effective *open hash set* implemented by myself several years ago. One can find it in the
      package **collection**. It works with the raw types, but not with the objects, which helps to use 4 times less
      memory while storing integers. So, if we have 50_000_000 lines in the input file (more than 1 GB file), then the
      max amount of uniq users is also 50_000_000. The amount of their corresponding integer hashes is the same(+-).
      And, therefore, the total memory consumption for all these 50_000_000 users will be 200 - 300 MB, which is very
      little. And it works much faster, than using a DB. Of course, this approach gives us not precise statistics, but
      very close to the truth, because there are possible collisions while taking hashes from users. This problem can be
      solved, for example, by taking hashes not onto integers, but onto longs.
- The frontend is located by the path "/statistics/unique-users-by-source" and shows a message to reload the page if
  the file is not handled yet.
- input and output files can be specified as the program arguments. If not specified, "input.csv" and "output.csv" used
  by default.
- I am sorry for the poor test coverage - just the lack of time.

-------------------------------------------------------------------------

## Description

Write a Java application, that counts users visited our web-page from different sources.  
Mostly filled with duplicates. A unique user is identified via unique phone and email combination.

As input you have a csv file, that contains:

```
email,phone,source
test@test.com,123,google.com
test@test.com,,google.com
test1@test.com,321,google.com
```

### The rules:

- The input file can be really huge (gigabytes)
- Ignore csv entries with any nullable field
- Just print the results to any output
- The application is not a single-use script, so should be designed to be supportable
- The filename should be passed in any convenient way, but should not be hardcoded.

### The bonus:

- solve the same problem in another language like javascript (node), python or go
- create a frontend that displays the result
