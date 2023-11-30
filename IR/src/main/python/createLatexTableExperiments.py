import pandas as pd

# Read the CSV file into a pandas DataFrame
df = pd.read_csv("/IR/data/output/descriptionExperiments.csv")

# Round the values in the DataFrame, only show 3 decimal places
df = df.round(2)

# Export the DataFrame as a LaTeX table
latex_table = df.to_latex(index=False)

# Print the LaTeX table
print(latex_table)

with open(
    "/Users/jonas/sciebo/MMDS/IE670 Web Data Integration/WDI Project/IR/data/output/descriptionExperiments.tex",
    "w",
) as f:
    f.write(latex_table)
