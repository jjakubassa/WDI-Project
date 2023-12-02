import pandas as pd

# Read the CSV file into a pandas DataFrame
df = pd.read_csv("data/output/summary_IR.csv", skipinitialspace=True)

# Sort by dataset
df = df.sort_values(by=["Dataset"])

# Convert ms to s
df["Time [s]"] = df["Time [ms]"] / 1000
df = df.drop(columns=["Time [ms]"])
df["Time [s]"] = df["Time [s]"].round(0)

# Export the DataFrame as a LaTeX table
latex_table = df.to_latex(index=False, float_format="%.2f")

# Print the LaTeX table
print(latex_table)

with open(
    "data/output/summary_IR.tex",
    "w",
) as f:
    f.write(latex_table)
