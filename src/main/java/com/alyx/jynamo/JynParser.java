//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo;

import com.google.common.base.Preconditions;
import joptsimple.*;

import java.util.List;

import static com.alyx.Log.log;

public class JynParser extends OptionParser {
    /**
     * Add a command line that cannot take an argument.
     */
    protected OptionSpec<Void> noArgOpt (String option, String desc) {
        return accepts(option, desc);
    }

    /**
     * Add a command line with an argument which may be either required or optional.
     */
    public OptionSpec<String> stringOpt (String option, String desc, String defVal) {
        return argOpt(option, desc, defVal).ofType(String.class);
    }

    /**
     * Add a command line with an argument which may be either required or optional.
     */
    public OptionSpec<Integer> intOpt (String option, String desc, Integer defVal) {
        return argOpt(option, desc, (defVal != null) ? defVal.toString() : null).ofType(Integer.class);
     }

    protected ArgumentAcceptingOptionSpec<String> argOpt (String option, String desc, String defVal) {
        OptionSpecBuilder builder = accepts(option, desc);

        ArgumentAcceptingOptionSpec<String> spec;
        if (defVal != null) {
            spec = builder.withOptionalArg().defaultsTo(new String[] { defVal });
        } else {
            spec = builder.withRequiredArg();
        }
        return spec;
    }

    public JynOptions jynParse (String... arguments) {
        return new JynOptions(parse(arguments));
    }

    public static class JynOptions {
        public JynOptions (OptionSet opts) {
            _opts = opts;
        }

        public <V> V require (OptionSpec<V> option) {
            V val = option.value(_opts);
            return Preconditions.checkNotNull(option.value(_opts),
                    "This operation requires the " + option.toString() + " option.");
        }

        public List<String> nonOptionArguments () {
            return _opts.nonOptionArguments();
        }
        public boolean has (OptionSpec<String> opt) {
            return _opts.has(opt);
        }

        public boolean has (String optName) {
            return _opts.has(optName);
        }

        public <V> V valueOf (OptionSpec<V> opt) {
            return _opts.valueOf(opt);
        }

        protected OptionSet _opts;
    }
}
