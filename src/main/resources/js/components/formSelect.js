export function fillSelect(select, items, valueKey, labelKey) {
    select.append(...items.map(item => createOption(item[valueKey],item[labelKey])));
}

function createOption(value, label) {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = label;
    return option;
}