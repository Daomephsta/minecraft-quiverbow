#Creates ammo model jsons from a list of comma separated names
import sys
import json
import string
import mcjson

modid = "quiverchevsky"

main_model_template = '{{"parent":"item/generated", "textures":{{"layer0":"{texture}"}}, "overrides":[{{"predicate": {{"damage": 1.0}}, "model": "{empty_model}"}}]}}'

empty_model_template = '{{"parent":"item/generated", "textures":{{"layer0":"{texture}"}}}}'

def gen_main_model(model_name):
	main_model = open(model_name + ".json", "w")
	raw_json = str.format(main_model_template, texture = modid + ":items/ammo/" + model_name, empty_model = modid + ":item/ammo/" + model_name + "_empty")
	parsed_json = json.loads(raw_json)
	main_model.write(json.dumps(parsed_json, indent = 4))
	main_model.close()

if len(sys.argv) < 2:
	print("ERR: Comma separated list of ammo names required\n")
	sys.exit()
model_names = str.split(sys.argv[1], ',')
for model_name in model_names:
	gen_main_model(model_name)
	mcjson.gen_basic_item_model(model_name + "_empty",  modid + ":items/ammo/" + model_name + "_empty")