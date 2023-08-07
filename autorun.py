import os
# os.system("make")
os.system("java -cp ./bin MonteCarloMini.MonteCarloMinimizationParallel 10000 10000 30 20 10 20 0.3")
os.system(
    "java -cp ./bin MonteCarloMini.MonteCarloMinimization 10000 10000 30 20 10 20 0.3")
# 10000 10000 0 10 0 10 1
