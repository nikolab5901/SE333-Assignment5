[![SE333_CI Status](https://github.com/nikolab5901/SE333-Assignment5/actions/workflows/SE333_CI.yml/badge.svg)](https://github.com/nikolab5901/SE333-Assignment5/actions/workflows/SE333_CI.yml)


## Project Overview
Added the tests for AmazonIntegrationtest and AmazonUnitTesting

## Build Status

When looking at the JOB_CLI output, the build status fails due to Maven issues with the files in the folder, as well as AmazonIntegrationTest having a test that fails when it is ran. 

## Part 4 comparisons

## Write a several paragraph reflection comparing: Manual UI testing: You wrote it in Java + Playwright

Manual UI testing in Playwright was difficult, yet very intuitive. The program was able to record the exact inputs I was making on the computer, and all I needed to do was use the completed run's code to generate the tests case and make a video recording out of it. While some issues came up, such as prices not mathching, as well as terms like "valid" causing the tests to fail/crash, after fixing these issues, I was able to get a succsessful video of going through the depaul store website. 


## AI-assisted UI testing: You prompted the MCP agent to generate Playwright tests. 
You can compare aspects like: 
- Ease of writing and running tests
- Accuracy and reliability of generated tests
- Maintenance effort
- Limitations or issues you encountered

Using the AI-assisted UI testing with the MCP agent was a lot more difficult, so much so that I do not think I was able to succsessfuly implement it. I installed Node.js, as well as the playwright add-ons and MCP tools, and prompted the agents to use them, but after creating the tests every time it was ran an error had occurd. I would try to explain what the error was to the LLM, but even after that, errors would occur. Because of this, I was not able to get a successful video run. 

It could be that I did not have my environment set up correctly, but I felt that manually going through the webpage, clicking on things myself, and having the program record my inputs to make test on worked a lot better, and was a lot more profecient in making test. I find that setting up the testing environment for the manual test was also a lot more simplier compared to setting up the LLM environemnt. 



