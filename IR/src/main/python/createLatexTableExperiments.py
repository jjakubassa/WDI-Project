import pandas as pd

# Read the CSV file into a pandas DataFrame
df = pd.read_csv("data/output/descriptionExperiments.csv")

# Round the values in the DataFrame, only show 3 decimal places
df = df.round(2)

# Export the DataFrame as a LaTeX table
latex_table = df.to_latex(index=False)

# Print the LaTeX table
print(latex_table)

with open(
    "data/output/descriptionExperiments.tex",
    "w",
) as f:
    f.write(latex_table)
