import math
import random

random.seed()

def get_latency_date(file_name, window_start, new_window_function):
    with open(file_name, "w") as f:

        for num_devices in range(100, 6001, 100):
            print(num_devices)
            avg_latency = 0

            for _ in range(0, 10):
                devices = num_devices
                window_size = window_start
                latency = 0

                while devices > 0:
                    device_choices = []
                    for _ in range(0, window_size):
                        device_choices.append(0)

                    for _ in range(0, devices):
                        device_choices[random.randrange(0, window_size)] += 1

                    for choice in device_choices:
                        if choice == 1:
                            devices -= 1
                            
                    latency += window_size
                    window_size = new_window_function(window_size)

                avg_latency += latency

            f.write(str(int(avg_latency/10)) + '\n')

def linear_function(window_size):
    return window_size + 1

def binary_function(window_size):
    return window_size * 2

def log_log_function(window_size):
    return int((1 + 1/math.log(math.log(window_size, 2), 2)) * window_size)

get_latency_date('linearLatency.txt', 2, linear_function)
get_latency_date('binaryLatency.txt', 2,  binary_function)
get_latency_date('loglogLatency.txt', 4, log_log_function)