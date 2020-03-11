import sys
import re
import math

#################################
########### Constants ###########
#################################

CALIBRATED_RSSI_DB = -59.9
RSSI_FACTOR = 2.8

A_KEY = "A"
H_KEY = "H"
R_KEY = "r"
Q_KEY = "q"
MEAN_KEY = "mean"
STD_DEV_KEY = "std_dev"

#################################
############ Globals ############
#################################

filtered_distance = 0.0
kalman_p = 0.0

#################################
########### Functions ###########
#################################


"""
Return dict :
{   "A"         : A value,
    "H"         : H value,
    "r"         : r value
    "q"         : q value,
    "mean"      : mean value,
    "std_dev"   : standard deviation value
}
"""
def optimize_kalman_configuration(raw_rssi_data, expected_distance):
    best_configuration = {MEAN_KEY : 10.0, STD_DEV_KEY : 10.0}
    for A_scaled in range(1, 40):
        A = float(A_scaled) * (2.5 / 100.0)
        for H_scaled in range(1, 40):
            H = float(H_scaled) * (2.5 / 100.0)
            for r_scaled in range(1, 40):
                r = float(r_scaled) * (2.5 / 10.0)
                for q_scaled in range(1, 40):
                    q = float(q_scaled) * (2.5 / 10.0)
                    [mean, std_dev] = process_data(A, H, r, q, raw_rssi_data)
                    if is_configuration_improved(expected_distance, best_configuration[MEAN_KEY], best_configuration[STD_DEV_KEY], mean, std_dev):
                        best_configuration[A_KEY] = A
                        best_configuration[H_KEY] = H
                        best_configuration[R_KEY] = r
                        best_configuration[Q_KEY] = q
                        best_configuration[MEAN_KEY] = mean
                        best_configuration[STD_DEV_KEY] = std_dev
                        print(best_configuration)
    return best_configuration

# Return [mean, standard_deviation]
def process_data(A, H, r, q, raw_rssi_data):
    global filtered_distance
    global kalman_p

    filtered_distance = 0.0
    kalman_p = 0.0
    distance_data = []
    for rssi in raw_rssi_data:
        distance = calculate_distance(rssi)
        distance = kalman_filter(A, H, r, q, distance)
        distance_data.append(distance)

    # print(distance_data)
    mean = sum(distance_data) / float(len(distance_data))
    std_dev = math.sqrt((sum([distance*distance for distance in distance_data]) - len(distance_data) * mean * mean) / float(len(distance_data) - 1))
    return [mean, std_dev]


def calculate_distance(rssi):
    return float(math.pow(10.0, (CALIBRATED_RSSI_DB - rssi) / (10.0 * RSSI_FACTOR)))


def kalman_filter(A, H, r, q, distance):
    global filtered_distance
    global kalman_p

    filtered_distance = (A * filtered_distance)
    kalman_p = (A * A * kalman_p) + q
    gain = (kalman_p * H) / ((kalman_p * H * H) + r)
    kalman_p = (1 - (gain * H)) * kalman_p
    filtered_distance = filtered_distance + (gain * (distance - (H * filtered_distance)))

    return filtered_distance;


def is_configuration_improved(expected_value, current_mean, current_std_dev, new_mean, new_std_dev):
    is_improved = False

    percent_error_current = percent_error(expected_value, current_mean)
    percent_error_new = percent_error(expected_value, new_mean)

    if (percent_error_current < 0.05) and (percent_error_new < 0.05):
        is_improved = (current_std_dev > new_std_dev)
    else:
        current_value = percent_error(expected_value, current_mean) * (expected_value + current_std_dev)
        new_value = percent_error(expected_value, new_mean) * (expected_value + new_std_dev)
        is_improved = (current_value > new_value);

    return is_improved


def percent_error(expected, actual):
    return abs(actual - float(expected)) / float(expected)


def parse_data(filename):
    csv_file = open(filename, "r")
    raw_rssi_data = []
    pattern = re.compile(r'(\d+),(-?\d+(\.\d+)?)\n')

    for line in csv_file:
        # print("line: " + line)
        line_match = pattern.match(line)
        if line_match is not None:
            # print("match: " + str(line_match.group(2)))
            rssi = float(line_match.group(2))
            raw_rssi_data.append(rssi)
    
    csv_file.close()
    return raw_rssi_data

def main():
    if len(sys.argv) != 3:
        print("Incorrect number of arguments. Pass in the CSV file of RSSI values and expected distance value")
        exit(0)
    filename = sys.argv[1]
    expected_distance = float(sys.argv[2])

    raw_rssi_data = parse_data(filename)
    # print(raw_rssi_data)
    best_configuration = optimize_kalman_configuration(raw_rssi_data, expected_distance)
    print(best_configuration)


if __name__ == "__main__":
    main()

