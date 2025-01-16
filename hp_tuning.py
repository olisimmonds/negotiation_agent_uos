import os
import subprocess
import random
import pandas as pd # pip install pandas
import numpy as np # pip install numpy
from bayes_opt import BayesianOptimization

PROPERTIES_FOLDER = "src\\main\\resources\\" if os.name == 'nt' else "./src/main/resources/"
PROPERTIES_PATH = PROPERTIES_FOLDER + "hyperparameter.properties"
LIB_FOLDER = "lib\\" if os.name == 'nt' else "./lib/"
TARGET_FOLDER = "target\\" if os.name == 'nt' else "./target/"
GENIUS_JAR_PATH = LIB_FOLDER + "genius-10.4.jar"
AGENT_JAR_PATH = TARGET_FOLDER + "coursework-1.0-SNAPSHOT-jar-with-dependencies.jar"
TOURNAMENT_FILE = "multilateraltournament_3.xml"
LOG_PATH = "log\\" if os.name == 'nt' else './log/'
SEPARATOR = ";" if os.name == 'nt' else ":"
CLASSPATH = f"{PROPERTIES_FOLDER}{SEPARATOR}{GENIUS_JAR_PATH}{SEPARATOR}{AGENT_JAR_PATH}"
print (CLASSPATH)

# use to read properties file
# out = {"finishTime": 0.95, ...}
def read_params():
    with open(PROPERTIES_PATH) as f:
        l = [line.split("=") for line in f.readlines()]
        return {key.strip(): float(val.strip()) for key, val in l}

# use to set the properties file
# props will be the original dict you got from read_props()
def set_params(params):
    with open(PROPERTIES_PATH, 'w') as f:
        lines = [f"{key}={val}\n" for key, val in params.items()]
        f.writelines(lines)

# run before the tournament
# builds the .jar file
def maven_clean_compile():
    if subprocess.call(["mvn", "clean", "package"]) != 0:
        raise Exception("There was a problem compiling")

def start_tournament(tournament_idx=1):
    args = ["java", "-cp", CLASSPATH, "genius.cli.Runner", TOURNAMENT_FILE, LOG_PATH + f"log{tournament_idx}"]

    if subprocess.call(args) != 0:
        raise Exception("There was an issue running the tournament")

def get_tournament_out(tournament_idx=1):
    total_util = 0.0
    df = pd.read_csv(LOG_PATH + f"log{tournament_idx}.csv", sep=';', header=1)

    for _, row in df.iterrows():
        if "Agent17" in row["Agent 1"]:
            total_util += float(row["Utility 1"])
        elif "Agent17" in row["Agent 2"]:
            total_util += float(row["Utility 2"])

    return total_util

def log_results(results):
    with open("random_search_results.txt", "w") as f:
        lines = [f"{k}\n{v}\n-------------------------\n" for k,v in results]
        f.writelines(lines)

def get_param_grid():
    return {
        "finishTime": list(np.arange(0, 1.01, 0.01, dtype='float').round(2)),
        "giveUpTime": list(np.arange(0, 1.01, 0.01, dtype='float').round(2)),
        "transitionTime": list(np.arange(0, 1.01, 0.01, dtype='float').round(2)),
        "boulwareBeta": list(np.arange(0, 1.01, 0.01, dtype='float').round(2)),
        "reservationValue": list(np.arange(0, 1.01, 0.01, dtype='float').round(2)),
        "maxListSize": list(range(50, 500, 10))
    }

def get_param_space_for_bayes():
    return {
        "finishTime": (0,1),
        "giveUpTime": (0,1),
        "transitionTime": (0,1),
        "boulwareBeta": (0,1),
        "reservationValue": (0,1),
        "maxListSize": (50,500)
    }

def random_search(evals=1000):
    results = []
    param_grid = get_param_grid()

    for i in range(evals):
        sample_params = {key: random.sample(val, 1)[0] for key, val in param_grid.items()}
        set_params(sample_params)

        # maven_clean_compile()
        start_tournament(tournament_idx=i)
        utility = get_tournament_out(tournament_idx=i)
        results.append((utility, sample_params))

    results.sort(reverse=True, key=lambda x: x[0])
    return results

def test_func(finishTime, giveUpTime, transitionTime, boulwareBeta, reservationValue, maxListSize):

    bayes_params = {
        "finishTime": finishTime,
        "giveUpTime": giveUpTime,
        "transitionTime": transitionTime,
        "boulwareBeta": boulwareBeta,
        "reservationValue": reservationValue,
        "maxListSize": maxListSize
    }

    set_params(bayes_params)

    # maven_clean_compile()
    start_tournament()
    utility = get_tournament_out()

    return utility

def bayesian_opptimisation(evals=1000):
    optimizer = BayesianOptimization(
        f=test_func,
        pbounds=get_param_space_for_bayes(),
        random_state=1,
    )

    optimizer.maximize(
        init_points=50,
        n_iter=500,
    )

    print(optimizer.max)

if __name__ == "__main__":
    bayesian_opptimisation(250)
    #or
    #log_results(random_search(250)