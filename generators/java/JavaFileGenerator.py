import os
from generators.Descriptor import Descriptor
from .Helpers import is_byte_type, is_enum_type, is_struct_type, get_generated_class_name
from .JavaEnumGenerator import JavaEnumGenerator
from .JavaClassGenerator import JavaClassGenerator


class JavaFileGenerator:
    """Java file generator"""
    enum_class_list = {}

    def __init__(self, schema, options):
        self.schema = schema
        self.current = None
        self.options = options
        self.code = []

    def __iter__(self):
        self.current = self.generate()
        return self

    def __next__(self):
        self.code = []
        code, name = next(self.current)
        return Descriptor(name + '.java', code)

    def prepend_copyright(self, copyright_file):
        if os.path.isfile(copyright_file):
            with open(copyright_file) as header:
                self.code = [line.strip() for line in header] + ['']

    def set_import(self):
        self.code += ['import java.lang.*;']
        self.code += ['import java.io.*;']
        self.code += ['import java.nio.*;']
        self.code += ['import catapult.builders.*;'] + ['']

    def set_package(self):
        self.code += ['package catapult.builders;'] + ['']

    def generate(self):

        for type_descriptor, value in self.schema.items():
            self.code = []
            self.prepend_copyright(self.options['copyright'])
            self.set_package()
            self.set_import()
            attribute_type = value['type']

            if is_byte_type(attribute_type):
                # Typeless environment, values will be directly assigned
                pass
            elif is_enum_type(attribute_type):
                JavaFileGenerator.enum_class_list[type_descriptor] = JavaEnumGenerator(
                    type_descriptor, self.schema, value)
            elif is_struct_type(attribute_type):
                # skip all the inline classes
                if type_descriptor.endswith('Body'):
                    continue
                new_class = JavaClassGenerator(
                    type_descriptor, self.schema, value['layout'],
                    JavaFileGenerator.enum_class_list)
                self.code += new_class.generate(value)
                yield self.code, get_generated_class_name(type_descriptor)

        # write all the enum last just in case there are 'dymanic values'
        for type_descriptor, enum_class in JavaFileGenerator.enum_class_list.items():
            self.code = []
            self.prepend_copyright(self.options['copyright'])
            self.set_package()
            self.set_import()
            self.code += enum_class.generate()
            yield self.code, get_generated_class_name(type_descriptor)

        return
