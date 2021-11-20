import matplotlib.pyplot as plt

def get_y_array(file):
   y_array = []

   f = open(file, "r")
   for y in f:
      y_array.append(int(y))

   return y_array

x_array = [*range(100, 6001, 100)]
plt.figure(figsize=(13.33,7.5))

plt.plot(x_array, get_y_array('linearLatency.txt'), 'ro', ms=5.0, label='Linear Backoff')
plt.plot(x_array, get_y_array('binaryLatency.txt'), 'bo', ms=5.0, label='Binary Exponential Backoff')
plt.plot(x_array, get_y_array('loglogLatency.txt'), 'go', ms=5.0, label='LogLog Backoff')

plt.legend(fontsize=14)

plt.xlabel('Number of Devices N', fontsize=16)
plt.ylabel('Average Latency', fontsize=16)

plt.savefig('backoffPlot.png', bbox_inches='tight')