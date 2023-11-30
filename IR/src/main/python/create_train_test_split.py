import pandas as pd
from sklearn.model_selection import train_test_split

file_ext = ".csv"
file_path = "data/goldstandard/"
file_names = [
    "gs_mb_spy",
    "gs_wdc_mb",
    "gs_wdc_spy",
]

for file in file_names:
    df = pd.read_csv(file_path + file + file_ext, names=["id1", "id2", "label"])

    train_df, test_df = train_test_split(
        df, test_size=0.2, random_state=42, stratify=df["label"]
    )
    train_df.to_csv(file_path + file + "_train" + file_ext, index=False, header=False)
    test_df.to_csv(file_path + file + "_test" + file_ext, index=False, header=False)
    print(file, "done")
