import os
import statistics
os.system("make")
# os.system("java -cp ./bin MonteCarloMini.MonteCarloMinimizationParallel 10000 10000 30 20 10 20 0.3")
times = []
for i in range(0, 5):
    os.system("java -Xmx6g -cp ./bin MonteCarloMini.MonteCarloMinimizationParallel 10 10 -1000 1000 -1000 1000 0.1 > out.txt")
    with open("out.txt") as f:
        fl = f.read()
        time = eval(fl[fl.find("Time")+6:fl.find("ms")])
        times.append(time)
        # print(time)

print(statistics.median(times), end='')
print(" ms")
